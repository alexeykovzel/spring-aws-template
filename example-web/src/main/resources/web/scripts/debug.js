import * as http from '/scripts/utils/http.js';
import { ready } from '/scripts/utils/common.js';

ready(() => {
    let params = new URLSearchParams(location.search);
    let exclude = params.get('exclude') ?? '';
    let from = params.get('from') ?? 0;
    let to = params.get('to') ?? 100;
    let url = `/debug/logs?from=${from}&to=${to}&exclude=${exclude}`;

    // fetch logs from the server
    http.getJson(url).then((logs) => {
        let main = document.querySelector('.logs');
        logs.forEach(json => {
            let log = new Log(json).build();
            main.appendChild(log);
        });
    });
});

class Log {
    constructor(json) {
        this.date = json.date;
        this.type = json.type;
        this.thread = json.thread;
        this.path = json.path;
        this.message = json.message;
        this.stack_trace = json.stack_trace;
        this.color = {
            'TRACE': '#8a50a9',
            'DEBUG': '#485aab',
            'INFO': '#69916a',
            'WARN': '#c5ae53',
            'ERROR': '#b44b4b',
        }[this.type]
    }

    build() {
        // format date (e.g. 12/31/69, 11:59 PM)
        let options = { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
        let logDate = new Date(this.date).toLocaleDateString('en-US', options);

        // include only the last path class
        let path = this.path.substring(this.path.lastIndexOf('.') + 1);
        if (path.includes('$')) path = path.substring(path.lastIndexOf('$') + 1);

        // insert log with its date, type, thread, path and message
        let log = document.createElement('p');
        log.innerHTML += `<span class="log-date">${logDate}</span>   `;
        log.innerHTML += `<span class="log-type" style="color: ${this.color}">${this.type}</span>   `;
        log.innerHTML += `---    [ <span class="log-thread">${this.thread}</span> ]   `;
        log.innerHTML += `<span class="log-path">${path}</span>   :   `;
        log.innerHTML += this.message;

        // show stack trace for error logs
        if (this.stack_trace) {
            let paths = log.stack_trace.split('\n');
            let stackTrace = document.createElement('div');
            stackTrace.classList.add('stack-trace');
            stackTrace.innerHTML = '<p>' + paths[0] + ' ...</p>';
            stackTrace.onclick = function () {
                stackTrace.innerHTML = paths.map(line => '<p>' + line + '</p>').join('');
                stackTrace.style.cursor = 'auto';
                stackTrace.onclick = null;
            }
            log.after(stackTrace);
        }
        return log
    }
}