<script lang="ts">
	import type { PageData } from './$types';
	import { formatMoney } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();

	const product = $derived(data.product);
	const images = $derived(product.images ?? []);
	const tags = $derived(product.tags ?? []);
	const priceText = $derived(formatMoney(product.priceMinor, product.currency));

	let activeIndex = $state(0);

	$effect(() => {
		if (images.length === 0) return;

		if (activeIndex >= images.length) {
			activeIndex = 0;
			return;
		}

		if (activeIndex === 0) {
			const coverIndex = images.findIndex((image) => image.cover);
			if (coverIndex > 0) {
				activeIndex = coverIndex;
			}
		}
	});

	const activeImage = $derived(images[activeIndex] ?? images[0] ?? null);

	const ratingText = $derived.by(() => {
		const score = product.voteSummary?.score ?? 0;

		if (score === 0) return 'No rating';
		if (score > 0) return `Positive (${score})`;
		return `Negative (${Math.abs(score)})`;
	});

	const primaryActionHref = $derived(
		data.session.isAuthenticated ? '/account/library' : '/auth/login'
	);

	const primaryActionLabel = $derived(
		data.session.isAuthenticated ? 'Open Library' : 'Sign In'
	);
</script>

<div class="full-bleed grid-page-bg flex-1">
	<div class="mx-auto max-w-[1320px] px-6 py-10 md:py-12">
		<div class="grid gap-6 xl:grid-cols-[minmax(0,1.15fr)_420px]">
			<section class="space-y-4">
				<div class="overflow-hidden border border-neutral-200 bg-white shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					{#if activeImage}
						<div class="overflow-hidden border border-neutral-200 bg-white shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
							{#if activeImage}
								<div class="flex min-h-[420px] items-center justify-center bg-[#f3f3f3] p-4">
									<img
										src={activeImage.url}
										alt={product.title}
										class="max-h-[760px] w-full object-contain"
									/>
								</div>
							{:else}
								<div class="flex min-h-[420px] items-center justify-center bg-neutral-100 text-sm text-neutral-500">
									No preview image
								</div>
							{/if}
						</div>
					{:else}
						<div class="flex aspect-[4/3] items-center justify-center bg-neutral-100 text-sm text-neutral-500">
							No preview image
						</div>
					{/if}
				</div>

				{#if images.length > 1}
					<div class="border border-neutral-200 bg-white p-4 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
						<div class="mb-3 flex items-center justify-between">
							<h2 class="text-sm font-black uppercase tracking-[0.12em] text-neutral-500">
								Gallery
							</h2>
							<p class="text-xs text-neutral-500">{images.length} image(s)</p>
						</div>

						<div class="grid grid-cols-4 gap-3 sm:grid-cols-5 lg:grid-cols-6">
							{#each images as image, index}
								<button
									type="button"
									onclick={() => (activeIndex = index)}
									class={`overflow-hidden border transition ${
										index === activeIndex
											? 'border-[#ff4646] ring-2 ring-[#ff4646]/20'
											: 'border-neutral-200 hover:border-neutral-400'
									}`}
								>
									<img
										src={image.url}
										alt={`${product.title} ${index + 1}`}
										class="aspect-square w-full object-cover"
									/>
								</button>
							{/each}
						</div>
					</div>
				{/if}
			</section>

			<aside class="space-y-4 xl:sticky xl:top-24 xl:self-start">
				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<div class="space-y-4">
						<div class="space-y-2">
							<p class="text-sm font-medium text-neutral-500">Marketplace product</p>

							<h1 class="text-4xl font-black tracking-tight text-[#1f1f1f]">
								{product.title}
							</h1>

							{#if product.shortDescription}
								<p class="text-sm leading-6 text-neutral-600">
									{product.shortDescription}
								</p>
							{/if}
						</div>

						{#if tags.length > 0}
							<div class="flex flex-wrap gap-2">
								{#each tags as tag}
									<span class="rounded-[8px] border border-neutral-300 px-3 py-1 text-xs font-semibold uppercase tracking-[0.08em] text-neutral-700">
										{tag.name}
									</span>
								{/each}
							</div>
						{/if}

						<div class="flex flex-wrap items-center gap-4 text-sm text-neutral-600">
							<span class="font-semibold text-neutral-800">{ratingText}</span>
						</div>

						<div class="border-y border-neutral-200 py-5">
							<p class="text-sm font-semibold uppercase tracking-[0.14em] text-neutral-500">
								Price
							</p>
							<p class="mt-2 text-4xl font-black text-[#ff4646]">{priceText}</p>
							<p class="mt-2 text-sm text-neutral-500">
								Access and checkout flow will continue through account pages.
							</p>
						</div>

						<div class="space-y-3">
							{#if !data.session.isAuthenticated}
								<a
									href="/auth/login"
									class="flex w-full items-center justify-center rounded-[6px] bg-[#ff4646] px-5 py-4 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
								>
									Sign In
								</a>
							{:else if data.isOwned}
								<a
									href={data.downloadHref ?? '/account/library'}
									class="flex w-full items-center justify-center rounded-[6px] bg-[#ff4646] px-5 py-4 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
								>
									Download
								</a>
							{:else}
								<form method="POST" action="?/buyNow">
									<button
										type="submit"
										class="flex w-full items-center justify-center rounded-[6px] bg-[#ff4646] px-5 py-4 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
									>
										Buy Now
									</button>
								</form>
							{/if}
						
							<a
								href="/products"
								class="flex w-full items-center justify-center rounded-[6px] border border-black px-5 py-4 text-sm font-bold text-black transition hover:bg-neutral-100"
							>
								Back to Products
							</a>
						</div>
					</div>
				</div>

				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h2 class="text-xl font-black tracking-tight text-[#1f1f1f]">Quick Details</h2>

					<div class="mt-4 space-y-3 text-sm">
						<div class="flex items-start justify-between gap-4">
							<span class="text-neutral-500">Rating</span>
							<span class="text-right font-semibold text-neutral-800">{ratingText}</span>
						</div>

						<div class="flex items-start justify-between gap-4">
							<span class="text-neutral-500">Images</span>
							<span class="text-right font-semibold text-neutral-800">{images.length}</span>
						</div>

						{#if product.currency}
							<div class="flex items-start justify-between gap-4">
								<span class="text-neutral-500">Currency</span>
								<span class="text-right font-semibold text-neutral-800">{product.currency}</span>
							</div>
						{/if}

						{#if product.productId}
							<div class="flex items-start justify-between gap-4">
								<span class="text-neutral-500">Product ID</span>
								<span class="text-right font-semibold text-neutral-800">#{product.productId}</span>
							</div>
						{/if}
					</div>
				</div>
			</aside>
		</div>

		<div class="mt-6 grid gap-6 xl:grid-cols-[minmax(0,1.15fr)_420px]">
			<section class="space-y-6">
				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Product Description</h2>

					<div class="mt-5 space-y-5">
						{#if product.description}
							<div class="whitespace-pre-line text-sm leading-8 text-neutral-700">
								{product.description}
							</div>
						{:else}
							<p class="text-sm text-neutral-500">No detailed description provided.</p>
						{/if}
					</div>
				</div>

				{#if images.length > 0}
					<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
						<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">More Previews</h2>

						<div class="mt-5 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
							{#each images as image, index}
								<button
									type="button"
									onclick={() => (activeIndex = index)}
									class="overflow-hidden border border-neutral-200 text-left transition hover:border-neutral-400"
								>
									<div class="flex aspect-[4/3] items-center justify-center bg-[#f3f3f3] p-2">
										<img
											src={image.url}
											alt={`${product.title} ${index + 1}`}
											class="h-full w-full object-contain"
										/>
									</div>
								</button>
							{/each}
						</div>
					</div>
				{/if}
			</section>

			<section class="space-y-6">
				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h2 class="text-2xl font-black tracking-tight text-[#1f1f1f]">Access</h2>
					<p class="mt-4 text-sm leading-7 text-neutral-600">
						Products remain connected to your account after purchase and updated versions
						remain available from your library.
					</p>
				</div>

				<div class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
					<h2 class="text-2xl font-black tracking-tight text-[#1f1f1f]">Delivery</h2>
					<ul class="mt-4 space-y-3 text-sm leading-7 text-neutral-600">
						<li>• Downloads are managed from your account library.</li>
						<li>• Updated versions stay available on the same account.</li>
						<li>• Stripe purchase flow will be connected later.</li>
					</ul>
				</div>
			</section>
		</div>
	</div>
</div>