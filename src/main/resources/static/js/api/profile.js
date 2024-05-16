const myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");
myHeaders.append("Authorization", `Bearer ${localStorage.getItem("access_token")}`);

export const getCardProductLikeList = async () => {
    const requestOptions = {
        method: "GET",
        headers: myHeaders
    };
    try {
        const response = await fetch(`/api/cardproduct/like`, requestOptions);
        const result = await response.json();
        return result.result;
    } catch (error) {
        console.log("getCardProductLike error: ", error);
        return null;
    }
}

export const getCardProducMembertLikeDelete = async (cardId) => {
    const raw = JSON.stringify({
        "cardId": cardId
    });
    const requestOptions = {
        method: "DELETE",
        headers: myHeaders,
        body: raw
    };
    try {
        const response = await fetch(`/api/cardproduct/memberlike`, requestOptions);
        const result = await response.json();
        return result.result;
    } catch (error) {
        console.log("getCardProducMembertLikeDelete error: ", error);
        return null;
    }
}

export const getRegistrationcardList = async (cardId) => {
    const requestOptions = {
        method: "GET",
        headers: myHeaders
    };
    try {
        const response = await fetch(`/api/registrationcard/list`, requestOptions);
        const result = await response.json();
        return result.result;
    } catch (error) {
        console.log("getRegistrationcardList error: ", error);
        return null;
    }
}

export const getRegistrationCardDelete = async (cardNumber) => {
    const raw = JSON.stringify({
        "cardNumber": cardNumber
    });
    const requestOptions = {
        method: "DELETE",
        headers: myHeaders,
        body: raw
    };
    try {
        const response = await fetch(`/api/registrationcard/delete`, requestOptions);
        const result = await response.json();
        return result.result;
    } catch (error) {
        console.log("getRegistrationCardDelete error: ", error);
        return null;
    }
}