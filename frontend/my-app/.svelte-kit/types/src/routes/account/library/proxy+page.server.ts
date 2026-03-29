// @ts-nocheck
import type { PageServerLoad } from './$types';
import { getBuyerLibrary } from '$lib/server/buyer';

export const load = async ({ locals, fetch, url }: Parameters<PageServerLoad>[0]) => {
	const items = locals.accessToken ? await getBuyerLibrary(fetch, locals.accessToken) : [];

	return {
		items,
		downloadError: url.searchParams.get('downloadError')
	};
};