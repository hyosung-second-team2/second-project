import * as suggestion from "./api/suggestion.js";

const currentDate = new Date();
const year = currentDate.getFullYear();
const month = String(currentDate.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 1을 더하고 문자열로 변환 후 2자리로 패딩
const yearAndMonth = `${year}-${month}`;
const categories = {
    mt1: "대형마트",
    cs2: "편의점",
    ac5: "학원",
    ol7: "주유",
    ct1: "문화",
    fd6: "외식",
    ce7: "카페",
    hp8: "병원"
};
document.addEventListener("DOMContentLoaded", () => {
    localStorage.setItem('clickedmenu', ".side_suggestion");
    CardRecommandation()
})

const CardRecommandation = async () => {
    const cardRecoList= await suggestion.getCardRecoList(yearAndMonth)
    console.log(cardRecoList)

    const slicedCardProductList = (cardRecoList.cardProductList).slice(0,2)
    console.log("slicedCardProductList : ",slicedCardProductList)

    const contentBox = document.querySelector(".content_box")
    slicedCardProductList.forEach((card) => {
        const timeContent = document.createElement('div');
        timeContent.classList.add('content', 'time_content');
        contentBox.appendChild(timeContent);

        const imageWrapper = document.createElement("div")
        imageWrapper.classList.add('imagewrapper');
        timeContent.appendChild(imageWrapper);

        const timeImgElement = document.createElement('img');
        timeImgElement.classList.add('img');
        timeImgElement.src = `/img/cardimg/카드${card.cardProductId}.jpg`;
        imageWrapper.appendChild(timeImgElement);

        const cardRect = imageWrapper.getBoundingClientRect();
        const cardWidth = cardRect.width;

        timeImgElement.onload = () => {
            const imgWidth = timeImgElement.width;
            const imgHeight = timeImgElement.height;
            const aspectRatio = imgWidth / imgHeight;

            if (aspectRatio > 1) {
                timeImgElement.style.transform = "rotate(90deg)";
                timeImgElement.style.height =`${cardWidth}px`;
                timeImgElement.style.width =`${aspectRatio*cardWidth}px`;
            }
            else  {
                timeImgElement.style.height = `${cardWidth/aspectRatio}px`;
                timeImgElement.style.width = `${cardWidth}px`;
            }
        };


        const timeDescriptionBox = document.createElement('div');
        timeDescriptionBox.classList.add('description_box');
        timeContent.appendChild(timeDescriptionBox);

        const discountData = cardRecoList.discountAmountByCategoryArr.find((discount) => discount.cardId === card.cardProductId);

        console.log("discountData : ",discountData)

        // 내용 채울 곳
        const cardtitle = document.createElement('p');
        cardtitle.classList.add('card_title');
        cardtitle.textContent = `${card.cardName}`;
        timeDescriptionBox.appendChild(cardtitle);

        const timeExpenseType = document.createElement('p');
        timeExpenseType.classList.add('expense_type');
        timeExpenseType.textContent = `총 ${Math.floor(discountData["통합할인액"]).toLocaleString()}원 혜택`;

        timeDescriptionBox.appendChild(timeExpenseType);

        const timeTypeDescription1 = document.createElement('p');
        timeTypeDescription1.classList.add('type_description');
        timeTypeDescription1.textContent = `피킹률: ${parseFloat(discountData["피킹률"]).toFixed(2)}%`;
        timeDescriptionBox.appendChild(timeTypeDescription1);

        const { cardId, 통합할인액, ...discounts } = discountData;
        const filteredDiscounts = Object.entries(discounts)
            .filter(([key, value]) => key !== '통합할인액' && value !== 0 &&  key !== '피킹률' &&  key !== '연회비') // 통합할인액과 값이 0인 항목은 제외합니다.
            .sort((a, b) => b[1] - a[1]) // 값을 기준으로 내림차순으로 정렬합니다.
            .slice(0, 5) // 상위 5개의 항목을 선택합니다.
            .reduce((obj, [key, value]) => {
                obj[key] = value;
                return obj;
            }, {});

        const categoryPikings = document.createElement('div');
        categoryPikings.classList.add('category_pikings');
        timeDescriptionBox.appendChild(categoryPikings);

        Object.entries(filteredDiscounts).forEach(([category, discount]) => {
            const categoryPiking = document.createElement('div');
            categoryPiking.classList.add('category_piking');

            const categoryDiv = document.createElement('div');
            categoryDiv.classList.add('category');
            categoryDiv.textContent = category === "언제나할인" ? "기타" : (category in categories ? categories[category] : category);

            const pikingP = document.createElement('p');
            pikingP.classList.add('piking');
            pikingP.textContent = `${Math.round(discount).toLocaleString()}원 할인`;

            categoryPiking.appendChild(categoryDiv);
            categoryPiking.appendChild(pikingP);
            categoryPikings.appendChild(categoryPiking);
        });

        const cardButton = document.createElement('button');
        cardButton.classList.add('cardButton');
        cardButton.innerHTML = "카드 상세"
        timeDescriptionBox.appendChild(cardButton);

        cardButton.addEventListener('click', () => {
            window.location.href = `carddetail?id=${card.cardProductId}`;
        });
    })
}

const categoryList = await suggestion.getCategoryNameList(yearAndMonth);
const expenditureRatio = await suggestion.getExpenditureRatioList(yearAndMonth);

const title = document.querySelector(".title")
title.innerHTML = `${localStorage.getItem("memberName")}님에게 추천하는 카드`

const blub = document.querySelector(".blub")
const pikingRate = document.querySelector(".pikingrate")

blub.addEventListener("click", () => {
    if (pikingRate.classList.contains("visible")) {
        pikingRate.classList.remove("visible");
        pikingRate.classList.add("unvisible");
    } else {
        pikingRate.classList.remove("unvisible");
        pikingRate.classList.add("visible");
    }
});

var options_pie = {
    series: expenditureRatio,
    chart: {
        width: 380,
        type: 'pie',
    },
    labels: categoryList,
    responsive: [{
        breakpoint: 480,
        options_pie: {
            chart: {
                height: "100%",
                width: "100%"
            },
            legend: {
                position: 'bottom'
            }
        }
    }]
};

var piechart = new ApexCharts(document.querySelector("#pie-chart"), options_pie);
piechart.render();

const CATEGORYNAME = await suggestion.getLineCategoryNameList(yearAndMonth);
const TOTALAMOUNT = await suggestion.getTotalAmountList(yearAndMonth);

const newCATEGORYNAME = CATEGORYNAME.map((category) => {
    return category.replace('카페/베이커리', "카페")
})

var options_line = {
    series: [
        {
            name: "Low - 2013",
            data: TOTALAMOUNT
        }
    ],
    chart: {
        height: "100%",
        width: "100%",
        type: 'line',
        dropShadow: {
            enabled: true,
            color: '#000',
            top: 18,
            left: 7,
            blur: 10,
            opacity: 0.2
        },
        zoom: {
            enabled: false
        },
        toolbar: {
            show: false
        }
    },
    colors: ['#77B6EA', '#545454'],
    dataLabels: {
        enabled: true,
    },
    stroke: {
        curve: 'smooth'
    },
    grid: {
        borderColor: '#e7e7e7',
        row: {
            colors: ['#f3f3f3', 'transparent'], // takes an array which will be repeated on columns
            opacity: 0.5
        },
    },
    markers: {
        size: 1
    },
    xaxis: {
        categories: newCATEGORYNAME,
        title: {
            text: '카테고리'
        }
    },
    yaxis: {
        title: {
            text: '지출횟수'
        },
        min: Math.min(...TOTALAMOUNT)? 0 : Math.min(...TOTALAMOUNT),
        max: Math.max(...TOTALAMOUNT) + 5,
        tickAmount: 7,
    },
    legend: {
        position: 'top',
        horizontalAlign: 'right',
        floating: true,
        offsetY: -25,
        offsetX: -5
    }
};

var linechart = new ApexCharts(document.querySelector("#line-chart"), options_line);
linechart.render();

// 카드 키워드 검색
const hdCardSearch = document.querySelector("#hd_card_search")

hdCardSearch.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && hdCardSearch.value) {
        localStorage.setItem("searchWord", hdCardSearch.value)
        window.location.href = "cardlist"
        hdCardSearch.value = ""
    }
});


const hdSearchImage = document.querySelector(".hd_search_image")

hdSearchImage.addEventListener("click", () => {
    if (hdCardSearch.value) {
        localStorage.setItem("searchWord", hdCardSearch.value)
        window.location.href = "cardlist"
        hdCardSearch.value = ""
    }
})