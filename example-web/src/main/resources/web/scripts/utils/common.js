export function ready(callback) {
    if (document.readyState !== "loading") callback();
    else document.addEventListener("DOMContentLoaded", callback);
}

export function capitalize(val) {
    return val.charAt(0).toUpperCase() + val.slice(1).toLowerCase();
}

export function formatNumber(num) {
    num = Math.abs(Number(num));
    if (num >= 1.0e+9) return (num / 1.0e+9).toFixed(2) + 'B';
    if (num >= 1.0e+6) return (num / 1.0e+6).toFixed(2) + 'M';
    if (num >= 1.0e+3) return (num / 1.0e+3).toFixed(2) + 'K';
    return num.toFixed(0);
}

export function formatMoney(val) {
    return '$' + val.toFixed(2);
}