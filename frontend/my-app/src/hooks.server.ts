import type { Handle } from '@sveltejs/kit';
import { BackendHttpError } from '$lib/server/backend';
import { clearAuthCookies, getAccessToken, getRefreshToken, setAuthCookies } from '$lib/server/cookies';
import { getCurrentUser, refreshSession } from '$lib/server/auth';

function guestSession() {
	return {
		isAuthenticated: false,
		user: null
	};
}

function isAuthFailure(error: unknown) {
	return error instanceof BackendHttpError && (error.status === 401 || error.status === 403);
}

export const handle: Handle = async ({ event, resolve }) => {
	event.locals.session = guestSession();
	event.locals.accessToken = null;

	const accessToken = getAccessToken(event.cookies);
	const refreshToken = getRefreshToken(event.cookies);

	if (accessToken) {
		try {
			const user = await getCurrentUser(event.fetch, accessToken);
			event.locals.session = {
				isAuthenticated: true,
				user
			};
			event.locals.accessToken = accessToken;

			return resolve(event);
		} catch (error) {
			if (!isAuthFailure(error)) {
				throw error;
			}
		}
	}

	if (refreshToken) {
		try {
			const refreshed = await refreshSession(event.fetch, refreshToken);
			setAuthCookies(event.cookies, refreshed);

			const user = await getCurrentUser(event.fetch, refreshed.accessToken);

			event.locals.session = {
				isAuthenticated: true,
				user
			};
			event.locals.accessToken = refreshed.accessToken;
		} catch (error) {
			if (isAuthFailure(error)) {
				clearAuthCookies(event.cookies);
			} else {
				throw error;
			}
		}
	} else {
		clearAuthCookies(event.cookies);
	}

	return resolve(event);
};