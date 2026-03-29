import type { PageServerLoad } from './$types';
import { getSellerMonthlySales } from '$lib/server/seller';

export const load: PageServerLoad = async ({ parent, locals, fetch }) => {
	const { dashboard } = await parent();

	const monthlySales = locals.accessToken
		? await getSellerMonthlySales(fetch, locals.accessToken)
		: [];

	return {
		dashboard,
		monthlySales
	};
};