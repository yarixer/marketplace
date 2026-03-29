<script lang="ts">
	import { formatMoney } from '$lib/utils/format';

	type ProductCardModel = {
		slug: string;
		title: string;
		shortDescription?: string | null;
		priceMinor: number;
		currency: string;
		sellerName: string;
		coverImageUrl?: string | null;
		ratingScore?: number;
		isNew?: boolean;
	};

	let { product }: { product: ProductCardModel } = $props();

	function ratingLabel(score: number | undefined) {
		const value = score ?? 0;

		if (value === 0) return 'No Rating';
		if (value > 0) return `Positive (${value})`;
		return `Negative (${Math.abs(value)})`;
	}
</script>

<a
	href={`/products/${product.slug}`}
	class="group flex h-full flex-col overflow-hidden border border-neutral-200 bg-[#f7f7f7] shadow-[0_6px_20px_rgba(0,0,0,0.04)] transition hover:-translate-y-[2px] hover:shadow-[0_12px_28px_rgba(0,0,0,0.08)]"
>
	<div class="bg-[#efefef]">
		{#if product.coverImageUrl}
			<img
				src={product.coverImageUrl}
				alt={product.title}
				class="aspect-[4/5] w-full object-cover"
			/>
		{:else}
			<div class="flex aspect-[4/5] items-center justify-center text-sm text-neutral-500">
				No preview
			</div>
		{/if}
	</div>

	<div class="flex flex-1 flex-col bg-[#f7f7f7] px-4 py-4">
		{#if product.isNew}
			<p class="text-[0.95rem] font-black uppercase tracking-[0.04em] text-[#ff4646]">New!</p>
		{/if}

		<h3 class="mt-2 line-clamp-2 text-[1.05rem] font-semibold leading-7 text-neutral-900">
			{product.title}
		</h3>

		{#if product.shortDescription}
			<p class="mt-2 line-clamp-2 text-sm leading-6 text-neutral-600">
				{product.shortDescription}
			</p>
		{/if}

		<div class="mt-4 text-sm text-neutral-700">
			{ratingLabel(product.ratingScore)}
		</div>

		<div class="mt-auto flex items-end justify-between gap-4 pt-6">
			<div class="min-w-0">
				<p class="text-sm text-neutral-600">
					By
					<span class="font-medium underline underline-offset-2">
						{product.sellerName}
					</span>
				</p>
			</div>

			<p class="shrink-0 text-[1.9rem] font-semibold leading-none text-[#ff4646]">
				{formatMoney(product.priceMinor, product.currency)}
			</p>
		</div>
	</div>
</a>