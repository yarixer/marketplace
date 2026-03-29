<script lang="ts">
	import type { PageData } from './$types';
	import HomeHero from '$lib/components/home/HomeHero.svelte';
	import ProductCard from '$lib/components/product/ProductCard.svelte';

	let { data }: { data: PageData } = $props();
</script>

<div class="full-bleed grid-page-bg">
	<div class="mx-auto max-w-[1600px] space-y-16 px-6 py-12">
		<HomeHero diamondSrc="/diamond.gif" />

		<section class="space-y-6 pb-8">
			<div class="flex flex-wrap items-center justify-between gap-4">
				<h2 class="text-3xl font-black tracking-tight">Latest Products</h2>

				<a
					href="/products"
					class="inline-flex items-center gap-2 px-1 py-2 text-sm font-semibold transition hover:text-neutral-600"
				>
					<span>View all products</span>
					<svg viewBox="0 0 24 24" class="h-4 w-4 fill-none stroke-current stroke-2">
						<path d="M5 12h14" />
						<path d="M13 5l7 7-7 7" />
					</svg>
				</a>
			</div>

			<div class="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-5">
				{#each data.latestProducts as product}
					<ProductCard {product} />
				{/each}
			</div>
		</section>
	</div>
</div>