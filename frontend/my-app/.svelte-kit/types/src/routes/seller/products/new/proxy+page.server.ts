// @ts-nocheck
import { fail, redirect } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { getPublicTags } from '$lib/server/public';
import { createSellerProduct, updateSellerDraft } from '$lib/server/seller';
import { majorStringToMinor } from '$lib/utils/money';

export const load = async ({ fetch }: Parameters<PageServerLoad>[0]) => {
	const availableTags = await getPublicTags(fetch);

	return {
		availableTags
	};
};

export const actions = {
	default: async ({ request, locals, fetch }: import('./$types').RequestEvent) => {
		if (!locals.accessToken) {
			return fail(401, {
				message: 'Unauthorized',
				values: {
					title: '',
					shortDescription: '',
					description: '',
					priceUsd: '',
					selectedTagSlugs: [] as string[]
				}
			});
		}

		const formData = await request.formData();

		const title = String(formData.get('title') ?? '').trim();
		const shortDescription = String(formData.get('shortDescription') ?? '').trim();
		const description = String(formData.get('description') ?? '').trim();
		const priceUsd = String(formData.get('priceUsd') ?? '').trim();
		const selectedTagSlugs = formData
			.getAll('tagSlugs')
			.map((value) => String(value))
			.filter(Boolean);

		if (!title || !shortDescription || !description || !priceUsd) {
			return fail(400, {
				message: 'All fields are required.',
				values: {
					title,
					shortDescription,
					description,
					priceUsd,
					selectedTagSlugs
				}
			});
		}

		let priceMinor: number;

		try {
			priceMinor = majorStringToMinor(priceUsd);
		} catch (error) {
			return fail(400, {
				message: error instanceof Error ? error.message : 'Invalid price.',
				values: {
					title,
					shortDescription,
					description,
					priceUsd,
					selectedTagSlugs
				}
			});
		}

		try {
			const created = await createSellerProduct(fetch, locals.accessToken, { title });

			await updateSellerDraft(fetch, locals.accessToken, created.productId, {
				title,
				shortDescription,
				description,
				priceMinor,
				tagSlugs: selectedTagSlugs
			});

			throw redirect(303, `/seller/products/${created.productId}`);
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					message: error.message,
					values: {
						title,
						shortDescription,
						description,
						priceUsd,
						selectedTagSlugs
					}
				});
			}

			throw error;
		}
	}
};;null as any as Actions;