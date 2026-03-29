import { fail } from '@sveltejs/kit';
import type { Actions, PageServerLoad } from './$types';
import { BackendHttpError } from '$lib/server/backend';
import { getPublicTags } from '$lib/server/public';
import {
	deleteSellerDraftImage,
	getSellerProduct,
	submitSellerReview,
	updateSellerDraft,
	uploadSellerArchive,
	uploadSellerImages
} from '$lib/server/seller';
import { majorStringToMinor } from '$lib/utils/money';

export const load: PageServerLoad = async ({ locals, fetch, params }) => {
	const productId = Number(params.productId);

	const [product, availableTags] = locals.accessToken
		? await Promise.all([getSellerProduct(fetch, locals.accessToken, productId), getPublicTags(fetch)])
		: [null, []];

	return {
		product,
		availableTags
	};
};

export const actions: Actions = {
	updateDraft: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { draftMessage: 'Unauthorized' });
		}

		const productId = Number(params.productId);
		const formData = await request.formData();

		const title = String(formData.get('title') ?? '').trim();
		const shortDescription = String(formData.get('shortDescription') ?? '').trim();
		const description = String(formData.get('description') ?? '').trim();
		const priceUsd = String(formData.get('priceUsd') ?? '').trim();
		const selectedTagSlugs = formData
			.getAll('tagSlugs')
			.map((value) => String(value))
			.filter(Boolean);

		let priceMinor: number;

		try {
			priceMinor = majorStringToMinor(priceUsd);
		} catch (error) {
			return fail(400, {
				draftMessage: error instanceof Error ? error.message : 'Invalid price.'
			});
		}

		try {
			await updateSellerDraft(fetch, locals.accessToken, productId, {
				title,
				shortDescription,
				description,
				priceMinor,
				tagSlugs: selectedTagSlugs
			});

			return {
				draftSuccess: true,
				draftMessage: 'Draft updated successfully.'
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					draftMessage: error.message
				});
			}

			throw error;
		}
	},

	uploadArchive: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { archiveMessage: 'Unauthorized' });
		}

		const productId = Number(params.productId);
		const formData = await request.formData();
		const file = formData.get('file');

		if (!(file instanceof File) || file.size === 0) {
			return fail(400, {
				archiveMessage: 'Archive file is required.'
			});
		}

		try {
			await uploadSellerArchive(fetch, locals.accessToken, productId, file);

			return {
				archiveSuccess: true,
				archiveMessage: 'Archive uploaded successfully.'
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					archiveMessage: error.message
				});
			}

			throw error;
		}
	},

	uploadImages: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { imagesMessage: 'Unauthorized' });
		}

		const productId = Number(params.productId);
		const formData = await request.formData();
		const files = formData
			.getAll('files')
			.filter((value): value is File => value instanceof File && value.size > 0);

		if (files.length === 0) {
			return fail(400, {
				imagesMessage: 'At least one image is required.'
			});
		}

		try {
			await uploadSellerImages(fetch, locals.accessToken, productId, files);

			return {
				imagesSuccess: true,
				imagesMessage: 'Images uploaded successfully.'
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					imagesMessage: error.message
				});
			}

			throw error;
		}
	},

	deleteImage: async ({ request, locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { deleteImageMessage: 'Unauthorized' });
		}

		const productId = Number(params.productId);
		const formData = await request.formData();
		const imageId = Number(formData.get('imageId') ?? '0');

		try {
			await deleteSellerDraftImage(fetch, locals.accessToken, productId, imageId);

			return {
				deleteImageSuccess: true,
				deleteImageMessage: 'Image deleted successfully.'
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					deleteImageMessage: error.message
				});
			}

			throw error;
		}
	},

	submitReview: async ({ locals, fetch, params }) => {
		if (!locals.accessToken) {
			return fail(401, { submitMessage: 'Unauthorized' });
		}

		const productId = Number(params.productId);

		try {
			await submitSellerReview(fetch, locals.accessToken, productId);

			return {
				submitSuccess: true,
				submitMessage: 'Revision submitted for review.'
			};
		} catch (error) {
			if (error instanceof BackendHttpError) {
				return fail(error.status, {
					submitMessage: error.message
				});
			}

			throw error;
		}
	}
};