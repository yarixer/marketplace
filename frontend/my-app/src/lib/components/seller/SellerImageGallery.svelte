<script lang="ts">
	import type { SellerProductImageResponse } from '$lib/types/api';

	let {
		images,
		message = null,
		success = false
	}: {
		images: SellerProductImageResponse[];
		message?: string | null;
		success?: boolean;
	} = $props();

	function confirmDelete() {
		return confirm('Delete this image?');
	}
</script>

<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
	<div class="space-y-2">
		<h3 class="text-2xl font-black tracking-tight text-[#1f1f1f]">Current Images</h3>
		<p class="text-sm leading-6 text-neutral-600">
			Existing images stay attached until you delete them. Current cover is marked below.
		</p>
	</div>

	{#if images.length > 0}
		<div class="mt-5 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
			{#each images as image}
				<div class="space-y-3 border border-neutral-200 bg-white p-3">
					<div class="relative overflow-hidden bg-neutral-100">
						<img
							src={image.url}
							alt={image.originalFilename}
							class="aspect-square w-full object-cover"
						/>

						{#if image.cover}
							<span class="absolute left-3 top-3 rounded-[6px] bg-[#ff4646] px-3 py-1 text-xs font-bold text-white">
								Cover
							</span>
						{/if}
					</div>

					<div class="space-y-1 text-sm">
						<p class="font-bold text-neutral-900">{image.originalFilename}</p>
						<p class="text-neutral-500">{image.mimeType}</p>
					</div>

					<form
						method="POST"
						action="?/deleteImage"
						onsubmit={(event) => {
							if (!confirmDelete()) {
								event.preventDefault();
							}
						}}
					>
						<input type="hidden" name="imageId" value={image.imageId} />
						<button
							type="submit"
							class="w-full rounded-[6px] border border-black px-4 py-2 text-sm font-bold transition hover:bg-neutral-100"
						>
							Delete Image
						</button>
					</form>
				</div>
			{/each}
		</div>
	{:else}
		<p class="mt-5 text-sm text-neutral-500">No images uploaded yet.</p>
	{/if}

	{#if message}
		<p class={`mt-5 px-4 py-3 text-sm ${success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
			{message}
		</p>
	{/if}
</div>