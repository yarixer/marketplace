<script lang="ts">
	import type { PageData } from './$types';
	import MonthlySalesChart from '$lib/components/seller/MonthlySalesChart.svelte';
	import { formatMoney } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();

	function ratingLabel(score: number) {
		if (score === 0) return 'No rating';
		if (score > 0) return `Positive (${score})`;
		return `Negative (${Math.abs(score)})`;
	}
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Stats</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			Track recent sales and performance by product.
		</p>
	</div>

	<MonthlySalesChart
		items={data.monthlySales}
		currency={data.dashboard.wallet.currency}
	/>

	<div class="space-y-4">
		<h3 class="text-2xl font-black tracking-tight">Products</h3>

		{#if data.productStats.length > 0}
			<div class="overflow-x-auto border border-neutral-200">
				<table class="min-w-full border-collapse text-left">
					<thead class="bg-neutral-50">
						<tr>
							<th class="px-5 py-4 text-sm font-bold">Product</th>
							<th class="px-5 py-4 text-sm font-bold">Status</th>
							<th class="px-5 py-4 text-sm font-bold">Price</th>
							<th class="px-5 py-4 text-sm font-bold">Sales</th>
							<th class="px-5 py-4 text-sm font-bold">Gross Revenue</th>
							<th class="px-5 py-4 text-sm font-bold">Rating</th>
						</tr>
					</thead>
					<tbody>
						{#each data.productStats as item}
							<tr class="border-t border-neutral-200">
								<td class="px-5 py-4">
									<div class="space-y-1">
										<a href={`/seller/products/${item.productId}`} class="font-bold hover:underline">
											{item.title}
										</a>
										<p class="text-sm text-neutral-500">{item.slug}</p>
									</div>
								</td>
								<td class="px-5 py-4 text-sm">{item.workflowStatus}</td>
								<td class="px-5 py-4 text-sm">
									{formatMoney(item.currentPriceMinor, data.dashboard.wallet.currency)}
								</td>
								<td class="px-5 py-4 text-sm">{item.salesCount}</td>
								<td class="px-5 py-4 text-sm">
									{formatMoney(item.grossRevenueMinor, data.dashboard.wallet.currency)}
								</td>
								<td class="px-5 py-4 text-sm">{ratingLabel(item.ratingScore)}</td>
							</tr>
						{/each}
					</tbody>
				</table>
			</div>
		{:else}
			<div class="border border-dashed border-neutral-300 px-8 py-16 text-center">
				<h2 class="text-xl font-bold">No product stats yet</h2>
				<p class="mt-2 text-sm text-neutral-600">
					Stats will appear after you publish products and get sales.
				</p>
			</div>
		{/if}
	</div>
</div>