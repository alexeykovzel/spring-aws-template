export function deleteRaw(url) {
    console.log('delete raw: ' + url);
    return request(url, {'method': 'DELETE'});
}

export function post(url) {
    console.log('post raw: ' + url);
    return request(url, {'method': 'POST'});
}

export function postJson(url, json) {
    console.log('post json: ' + url);
    return request(url, {
        'method': 'POST',
        'headers': {'Content-Type': 'application/json'},
        'body': JSON.stringify(json)
    });
}

export function postForm(url, form) {
    console.log('post form: ' + url);
    return request(url, {
        'method': 'POST',
        'headers': {'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'},
        'body': new URLSearchParams(form),
    });
}

export function getText(url) {
    return request(url, {
        'method': 'GET',
        'headers': {'Accept': 'text/plain'},
    }).then(data => {
        if (!data) throw new Error('Invalid data');
        return data.text();
    });
}

export function getJson(url) {
    return request(url, {
        'method': 'GET',
        'headers': {'Accept': 'application/json'},
    }).then(data => {
        if (!data) throw new Error('Invalid data');
        return data.json();
    });
}

function request(url, props) {
    return fetch(location.origin + url, props)
        .then(response => {
            if (response.redirected) throw new Error('Tried to redirect')
            if (!response.ok) throw new Error(response.statusText);
            return response;
        });
}