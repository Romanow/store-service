import {URLSearchParams} from "https://jslib.k6.io/url/1.0.0/index.js";
import {randomItem} from "https://jslib.k6.io/k6-utils/1.2.0/index.js";
import http from "k6/http";
import {check} from "k6";

export const options = {
    stages: [
        {duration: "20s", target: 10},
        {duration: "30s", target: 20},
        {duration: "1m30s", target: 50},
        {duration: "20s", target: 10},
    ],
};

export function setup() {
    const body = new URLSearchParams({
        "scope": "openid",
        "grant_type": "password",
        "username": `${__ENV.USERNAME}`,
        "password": `${__ENV.PASSWORD}`,
        "client_id": `${__ENV.CLIENT_ID}`,
        "client_secret": `${__ENV.CLIENT_SECRET}`
    })
    const params = {
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        }
    }

    const url = __ENV.IDENTITY_PROVIDER
    const result = http.post(url, body.toString(), params)
    return result.json().access_token
}

export default function (token) {
    const params = {
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    };

    // step 1: list items
    let response = http.get("http://localhost:8080/warehouse/api/public/v1/items");
    check(response, {
        "status is OK": (r) => r.status === 200,
        "content is present": (r) => !!r.body,
    });
    const item = [randomItem(response.json("#.name"))]

    // step 2: purchase
    response = http.post("http://localhost:8080/store/api/protected/v1/orders/purchase", JSON.stringify(item), params);
    check(response, {
        "status is OK": (r) => r.status === 201,
    });
    const orderUid = response.headers["Location"].split("/").pop()

    // step 3: order info by user
    response = http.get(`http://localhost:8080/store/api/protected/v1/orders/${orderUid}`, params);
    check(response, {
        "status is OK": (r) => r.status === 200,
        "content is present": (r) => !!r.body,
    });

    // step 4: warranty request
    response = http.post(`http://localhost:8080/store/api/protected/v1/orders/${orderUid}/warranty`, JSON.stringify(item), params);
    check(response, {
        "status is OK": (r) => r.status === 200,
        "content is present": (r) => !!r.body,
    });

    // step 5: return order
    response = http.del(`http://localhost:8080/store/api/protected/v1/orders/${orderUid}/cancel`, null, params);
    check(response, {
        "status is NO_CONTENT": (r) => r.status === 202
    });
};
