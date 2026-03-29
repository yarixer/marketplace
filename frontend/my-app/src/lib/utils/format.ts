export function formatMoney(minor: number, currency: string) {
	return new Intl.NumberFormat('en-US', {
		style: 'currency',
		currency
	}).format(minor / 100);
}

export function formatDateTime(value: string | Date) {
	const date = value instanceof Date ? value : new Date(value);

	return new Intl.DateTimeFormat('en-US', {
		year: 'numeric',
		month: 'short',
		day: '2-digit',
		hour: '2-digit',
		minute: '2-digit'
	}).format(date);
}