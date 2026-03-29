<script lang="ts">
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	function ratingLabel(score: number) {
		if (score === 0) return 'No rating';
		if (score > 0) return `Positive (${score})`;
		return `Negative (${Math.abs(score)})`;
	}
</script>

<div class="space-y-8">
	<div class="flex flex-wrap items-start justify-between gap-6">
		<div>
			<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Products</h2>
			<p class="mt-2 text-sm leading-6 text-neutral-600">
				Create products, track statuses and open the editor.
			</p>
		</div>

		<a
			href="/seller/products/new"
			class="rounded-[6px] bg-[#ff4646] px-5 py-3 text-sm font-bold text-white transition hover:bg-[#f03a3a]"
		>
			New Product
		</a>
	</div>

	{#if data.items.length > 0}
		<div class="overflow-x-auto border border-neutral-200">
			<table class="min-w-full border-collapse text-left">
				<thead class="bg-neutral-50">
					<tr>
						<th class="px-5 py-4 text-sm font-bold">Title</th>
						<th class="px-5 py-4 text-sm font-bold">Status</th>
						<th class="px-5 py-4 text-sm font-bold">Rating</th>
						<th class="px-5 py-4 text-sm font-bold">Updated</th>
						<th class="px-5 py-4 text-sm font-bold">Action</th>
					</tr>
				</thead>
				<tbody>
					{#each data.items as item}
						<tr class="border-t border-neutral-200">
							<td class="px-5 py-4">
								<div class="space-y-1">
									<p class="font-bold">{item.title}</p>
									<p class="text-sm text-neutral-500">{item.slug}</p>
								</div>
							</td>
							<td class="px-5 py-4 text-sm font-medium">{item.workflowStatus}</td>
							<td class="px-5 py-4 text-sm">{ratingLabel(item.ratingScore)}</td>
							<td class="px-5 py-4 text-sm">{new Date(item.updatedAt).toLocaleString()}</td>
							<td class="px-5 py-4">
								<a
									href={`/seller/products/${item.productId}`}
									class="inline-flex rounded-[6px] border border-black px-4 py-2 text-sm font-bold transition hover:bg-neutral-100"
								>
									Edit
								</a>
							</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{:else}
		<div class="border border-dashed border-neutral-300 px-8 py-16 text-center">
			<h2 class="text-xl font-bold">No products yet</h2>
			<p class="mt-2 text-sm text-neutral-600">
				Create your first product to open the seller flow.
			</p>
		</div>
	{/if}
</div>