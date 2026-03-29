import { redirect, type RequestHandler } from '@sveltejs/kit';
import { clearAuthCookies } from '$lib/server/cookies';

export const POST: RequestHandler = async ({ cookies, url }) => {
	clearAuthCookies(cookies);

	const redirectTo = url.searchParams.get('redirectTo') || '/';
	throw redirect(303, redirectTo);
};