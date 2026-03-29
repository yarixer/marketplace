import type { PageServerLoad } from './$types';
import { getBuyerLibrary } from '$lib/server/buyer';

export const load: PageServerLoad = async ({ locals, fetch, url }) => {
	const items = locals.accessToken ? await getBuyerLibrary(fetch, locals.accessToken) : [];

	return {
		items,
		downloadError: url.searchParams.get('downloadError')
	};
};