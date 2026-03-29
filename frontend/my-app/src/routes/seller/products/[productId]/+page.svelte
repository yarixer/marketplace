<script lang="ts">
	import type { ActionData, PageData } from './$types';
	import SellerAutoUploadForm from '$lib/components/seller/SellerAutoUploadForm.svelte';
	import SellerImageGallery from '$lib/components/seller/SellerImageGallery.svelte';
	import SellerProductForm from '$lib/components/seller/SellerProductForm.svelte';
	import SellerWorkflowCard from '$lib/components/seller/SellerWorkflowCard.svelte';
	import { minorToMajorString } from '$lib/utils/money';

	let { data, form }: { data: PageData; form: ActionData } = $props();

	const product = $derived(data.product);
	const draft = $derived(product?.draftRevision);
	const pending = $derived(product?.pendingRevision);
	const live = $derived(product?.liveRevision);
	const editableRevision = $derived(draft ?? pending ?? live ?? null);
	const assetRevision = $derived(draft ?? pending ?? live ?? null);

	const archiveTitle = $derived(assetRevision?.archiveOriginalFilename ?? null);
	const archiveSubtitle = $derived(
		assetRevision?.archiveAttached && assetRevision.archiveSizeBytes
			? `${assetRevision.archiveSizeBytes} bytes`
			: null
	);

	const galleryImages = $derived(assetRevision?.images ?? []);
</script>

{#if product && editableRevision}
	<div class="space-y-8">
		<div class="flex flex-wrap items-start justify-between gap-5">
			<div>
				<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">
					{editableRevision.title}
				</h2>
				<p class="mt-2 text-sm leading-6 text-neutral-600">
					Edit product details, replace files and prepare the next review submission.
				</p>
			</div>
		</div>

		<div class="grid gap-6 xl:grid-cols-[minmax(0,1.12fr)_360px]">
			<section class="space-y-6">
				<SellerProductForm
					action="?/updateDraft"
					submitLabel="Save Draft"
					availableTags={data.availableTags}
					values={{
						title: editableRevision.title ?? '',
						shortDescription: editableRevision.shortDescription ?? '',
						description: editableRevision.description ?? '',
						priceUsd: minorToMajorString(editableRevision.priceMinor ?? 0),
						selectedTagSlugs: editableRevision.tags?.map((tag) => tag.slug) ?? []
					}}
					message={form?.draftMessage ?? null}
					success={form?.draftSuccess ?? false}
				/>

				<SellerAutoUploadForm
					action="?/uploadArchive"
					title="Archive"
					description="Replace or upload the ZIP for the current draft. The upload starts immediately after file selection."
					inputId="archiveFile"
					inputName="file"
					accept=".zip,application/zip"
					dropLabel="Choose ZIP archive"
					dropHint="Click to select a .zip file"
					currentTitle={archiveTitle}
					currentSubtitle={archiveSubtitle}
					message={form?.archiveMessage ?? null}
					success={form?.archiveSuccess ?? false}
				/>

				<SellerAutoUploadForm
					action="?/uploadImages"
					title="Preview Images"
					description="Add new preview images. Upload starts immediately and current images stay attached until deleted."
					inputId="imageFiles"
					inputName="files"
					accept=".png,.jpg,.jpeg,.webp,image/png,image/jpeg,image/webp"
					multiple={true}
					dropLabel="Choose preview images"
					dropHint="PNG, JPG, JPEG, WEBP · multiple files allowed"
					message={form?.imagesMessage ?? null}
					success={form?.imagesSuccess ?? false}
				/>

				<SellerImageGallery
					images={galleryImages}
					message={form?.deleteImageMessage ?? null}
					success={form?.deleteImageSuccess ?? false}
				/>
			</section>

			<SellerWorkflowCard
				{product}
				submitMessage={form?.submitMessage ?? null}
				submitSuccess={form?.submitSuccess ?? false}
			/>
		</div>
	</div>
{/if}