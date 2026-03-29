import { env } from '$env/dynamic/private';

function normalizeUrl(value: string) {
	return value.replace(/\/+$/, '');
}

export function getServerEnv() {
	const backendBaseUrl = normalizeUrl(env.BACKEND_BASE_URL ?? 'http://localhost:8080');

	if (!/^https?:\/\//.test(backendBaseUrl)) {
		throw new Error('BACKEND_BASE_URL must start with http:// or https://');
	}

	return {
		backendBaseUrl
	};
}