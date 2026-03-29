export function minorToMajorString(minor: number | null | undefined) {
	if (minor === null || minor === undefined || Number.isNaN(minor)) {
		return '';
	}

	return (minor / 100).toFixed(2);
}

export function majorStringToMinor(value: string) {
	const normalized = value.trim().replace(',', '.');

	if (!normalized) {
		throw new Error('Price is required.');
	}

	if (!/^\d+(\.\d{1,2})?$/.test(normalized)) {
		throw new Error('Price must be a valid dollar amount with up to 2 decimals.');
	}

	const numeric = Number(normalized);

	if (!Number.isFinite(numeric) || numeric <= 0) {
		throw new Error('Price must be greater than 0.');
	}

	return Math.round(numeric * 100);
}