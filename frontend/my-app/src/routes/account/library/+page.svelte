<script lang="ts">
	import type { PageData } from './$types';
	import { formatDateTime, formatMoney } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Library</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			All purchased products available on your account.
		</p>
	</div>

	{#if data.downloadError}
		<p class="bg-red-50 px-4 py-3 text-sm text-red-700">
			{data.downloadError}
		</p>
	{/if}

	{#if data.items.length > 0}
		<div class="grid gap-5">
			{#each data.items as item}
				<div class="border border-neutral-200 bg-white p-5">
					<div class="flex flex-wrap items-start justify-between gap-5">
						<div class="min-w-0 flex-1">
							<div class="flex flex-wrap items-center gap-3">
								<a href={`/products/${item.slug}`} class="text-xl font-black hover:underline">
									{item.title}
								</a>

								{#if item.downloadAvailable}
									<span class="bg-green-50 px-3 py-1 text-xs font-bold text-green-700">
										Download ready
									</span>
								{:else}
									<span class="bg-neutral-100 px-3 py-1 text-xs font-bold text-neutral-600">
										No archive
									</span>
								{/if}
							</div>

							<p class="mt-2 max-w-3xl text-sm leading-6 text-neutral-600">
								{item.shortDescription}
							</p>

							<div class="mt-4 flex flex-wrap gap-5 text-sm text-neutral-600">
								<div>
									<span class="font-semibold text-black">Seller:</span>
									{item.sellerPublicName}
								</div>

								<div>
									<span class="font-semibold text-black">Granted:</span>
									{formatDateTime(item.grantedAt)}
								</div>

								<div>
									<span class="font-semibold text-black">Price:</span>
									{formatMoney(item.priceMinor, item.currency)}
								</div>
							</div>
						</div>

						<div class="flex shrink-0 flex-col gap-3">
							<a
								href={`/products/${item.slug}`}
								class="border border-black bg-white px-5 py-3 text-sm font-bold text-black transition hover:bg-neutral-100"
							>
								View Product
							</a>

							{#if item.downloadAvailable}
								<a
									href={`/account/library/download/${item.productId}`}
									class="bg-[#ff4646] px-5 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
								>
									Download
								</a>
							{/if}
						</div>
					</div>
				</div>
			{/each}
		</div>
	{:else}
		<div class="border border-dashed border-neutral-300 px-8 py-16 text-center">
			<h2 class="text-xl font-bold">Your library is empty</h2>
			<p class="mt-2 text-sm text-neutral-600">
				Purchased products will appear here and stay available for download.
			</p>
		</div>
	{/if}
</div>