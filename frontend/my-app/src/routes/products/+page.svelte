<script lang="ts">
	import type { PageData } from './$types';
	import Pagination from '$lib/components/product/Pagination.svelte';
	import ProductCard from '$lib/components/product/ProductCard.svelte';
	import ProductFilters from '$lib/components/product/ProductFilters.svelte';

	let { data }: { data: PageData } = $props();

	const sortOptions = [
		{ value: 'relevance', label: 'Relevance' },
		{ value: 'price_down', label: 'Price down' },
		{ value: 'price_up', label: 'Price up' },
		{ value: 'rating', label: 'Rating' }
	];

	let filtersOpen = $state(false);
	let sortOpen = $state(false);

	let filtersRoot = $state<HTMLDivElement | null>(null);
	let sortRoot = $state<HTMLDivElement | null>(null);

	function toggleFilters() {
		filtersOpen = !filtersOpen;
	}

	function toggleSort() {
		sortOpen = !sortOpen;
	}

	function closeAll() {
		filtersOpen = false;
		sortOpen = false;
	}

	function handleDocumentClick(event: MouseEvent) {
		const target = event.target;
		if (!(target instanceof Node)) return;

		if (filtersOpen && filtersRoot && !filtersRoot.contains(target)) {
			filtersOpen = false;
		}

		if (sortOpen && sortRoot && !sortRoot.contains(target)) {
			sortOpen = false;
		}
	}
</script>

<svelte:document onclick={handleDocumentClick} />

<div class="full-bleed grid-page-bg flex-1">
	<div class="mx-auto max-w-[1600px] space-y-8 px-6 py-10">
		<div class="flex flex-wrap items-center justify-between gap-4">
			<h1 class="text-3xl font-black tracking-tight">Products</h1>

			<div class="flex flex-wrap items-center gap-4">
				<div class="relative" bind:this={filtersRoot}>
					<button
						type="button"
						onclick={toggleFilters}
						class="flex h-[46px] items-center gap-3 rounded-[8px] border border-black bg-white px-4 text-[15px] font-semibold text-black transition hover:bg-neutral-50"
					>
						<svg viewBox="0 0 24 24" class="h-5 w-5 fill-none stroke-current stroke-2">
							<path d="M4 7h16" />
							<path d="M7 12h10" />
							<path d="M10 17h4" />
							<circle cx="8" cy="7" r="1.2" fill="currentColor" stroke="none" />
							<circle cx="15" cy="12" r="1.2" fill="currentColor" stroke="none" />
							<circle cx="12" cy="17" r="1.2" fill="currentColor" stroke="none" />
						</svg>
						<span>All Filters</span>
					</button>

					{#if filtersOpen}
						<div
							class="fixed inset-y-0 left-0 z-40 w-[320px] overflow-y-auto border-r border-black bg-white p-5 shadow-[0_20px_60px_rgba(0,0,0,0.12)]"
						>
							<div class="mb-4 flex items-center justify-between">
								<h2 class="text-lg font-black">Filters</h2>
								<button type="button" onclick={closeAll} class="text-sm font-medium">
									Close
								</button>
							</div>

							<ProductFilters
								tags={data.availableTags}
								selectedTags={data.filters.tags}
								currentSort={data.filters.sort}
								currentQuery={data.filters.q}
							/>
						</div>
					{/if}
				</div>

				<div class="relative" bind:this={sortRoot}>
					<button
						type="button"
						onclick={toggleSort}
						class="flex h-[46px] items-center gap-3 rounded-[8px] border border-black bg-white px-4 text-[15px] font-semibold text-black transition hover:bg-neutral-50"
					>
						<span>{sortOptions.find((option) => option.value === data.filters.sort)?.label ?? 'Relevance'}</span>
						<svg viewBox="0 0 24 24" class="h-4 w-4 fill-none stroke-current stroke-2">
							<path d="M6 9l6 6 6-6" />
						</svg>
					</button>

					{#if sortOpen}
						<div class="absolute right-0 top-[calc(100%+0.5rem)] z-40 min-w-[190px] rounded-[8px] border border-black bg-white shadow-[0_20px_60px_rgba(0,0,0,0.12)]">
							{#each sortOptions as option}
								<a
									href={`/products?q=${encodeURIComponent(data.filters.q)}&sort=${option.value}${data.filters.tags.map((tag) => `&tags=${encodeURIComponent(tag)}`).join('')}`}
									class={`block px-4 py-3 text-sm transition hover:bg-neutral-100 ${
										option.value === data.filters.sort ? 'font-bold' : 'font-medium'
									}`}
									onclick={closeAll}
								>
									{option.label}
								</a>
							{/each}
						</div>
					{/if}
				</div>
			</div>
		</div>

		{#if data.products.items.length > 0}
			<div class="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-5">
				{#each data.products.items as product}
					<ProductCard {product} />
				{/each}
			</div>

			<Pagination
				page={data.products.page}
				totalPages={data.products.totalPages}
				baseUrl={data.currentUrl}
			/>
		{:else}
			<div class="border border-dashed border-neutral-300 bg-white px-8 py-16 text-center">
				<h2 class="text-xl font-bold">No products found</h2>
				<p class="mt-2 text-sm text-neutral-600">
					Try changing your search query, tags or sorting.
				</p>
			</div>
		{/if}
	</div>
</div>