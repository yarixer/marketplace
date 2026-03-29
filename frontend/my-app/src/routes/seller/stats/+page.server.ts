import type { PageServerLoad } from './$types';
import { getSellerMonthlySales, getSellerProductStats } from '$lib/server/seller';

export const load: PageServerLoad = async ({ locals, fetch, parent }) => {
	const { dashboard } = await parent();

	const [monthlySales, productStats] = locals.accessToken
		? await Promise.all([
				getSellerMonthlySales(fetch, locals.accessToken),
				getSellerProductStats(fetch, locals.accessToken)
			])
		: [[], []];

	return {
		dashboard,
		monthlySales,
		productStats
	};
};