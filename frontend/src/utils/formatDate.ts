export const formatDate = (arr) => {
    if (!arr || arr.length < 5) return "Unknown date";
    const [year, month, day, hour, minute, second = 0] = arr;
    return new Date(year, month - 1, day, hour, minute, second).toLocaleDateString();
};
