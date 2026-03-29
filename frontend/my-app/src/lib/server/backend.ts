import type { ApiErrorResponse } from '$lib/types/api';
import { getServerEnv } from '$lib/server/env';

export class BackendHttpError extends Error {
	status: number;
	body: ApiErrorResponse | unknown | null;

	constructor(status: number, message: string, body: ApiErrorResponse | unknown | null) {
		super(message);
		this.name = 'BackendHttpError';
		this.status = status;
		this.body = body;
	}
}

type BackendRequestOptions = {
	method?: string;
	headers?: HeadersInit;
	token?: string | null;
	json?: unknown;
	body?: BodyInit | null;
};

function buildBackendUrl(path: string) {
	const normalizedPath = path.startsWith('/') ? path : `/${path}`;
	return `${getServerEnv().backendBaseUrl}${normalizedPath}`;
}

async function readResponseBody(response: Response) {
	if (response.status === 204) {
		return null;
	}

	const contentType = response.headers.get('content-type') ?? '';
	const text = await response.text();

	if (!text) {
		return null;
	}

	if (contentType.includes('application/json')) {
		try {
			return JSON.parse(text);
		} catch {
			return text;
		}
	}

	return text;
}

function extractErrorMessage(body: unknown, fallback: string) {
	if (body && typeof body === 'object' && 'message' in body) {
		const message = (body as { message?: unknown }).message;
		if (typeof message === 'string' && message.trim().length > 0) {
			return message;
		}
	}

	return fallback;
}

export async function backendRequest<T>(
	fetchFn: typeof fetch,
	path: string,
	options: BackendRequestOptions = {}
): Promise<T> {
	const headers = new Headers(options.headers);

	if (options.token) {
		headers.set('authorization', `Bearer ${options.token}`);
	}

	let body: BodyInit | null | undefined = options.body;

	if (options.json !== undefined) {
		headers.set('content-type', 'application/json');
		body = JSON.stringify(options.json);
	}

	const response = await fetchFn(buildBackendUrl(path), {
		method: options.method ?? 'GET',
		headers,
		body
	});

	const parsedBody = await readResponseBody(response);

	if (!response.ok) {
		throw new BackendHttpError(
			response.status,
			extractErrorMessage(parsedBody, response.statusText || 'Backend request failed'),
			parsedBody
		);
	}

	return parsedBody as T;
}