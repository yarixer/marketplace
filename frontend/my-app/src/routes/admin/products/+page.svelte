<script lang="ts">
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Products</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			Inspect all products, workflow state and revisions.
		</p>
	</div>

	{#if data.items.length > 0}
		<div class="overflow-x-auto border border-neutral-200">
			<table class="min-w-full border-collapse text-left">
				<thead class="bg-neutral-50">
					<tr>
						<th class="px-5 py-4 text-sm font-bold">Product</th>
						<th class="px-5 py-4 text-sm font-bold">Seller</th>
						<th class="px-5 py-4 text-sm font-bold">State</th>
						<th class="px-5 py-4 text-sm font-bold">Workflow</th>
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
							<td class="px-5 py-4 text-sm">{item.sellerPublicName}</td>
							<td class="px-5 py-4 text-sm">{item.state}</td>
							<td class="px-5 py-4 text-sm">{item.workflowStatus}</td>
							<td class="px-5 py-4">
								<a
									href={`/admin/products/${item.productId}`}
									class="inline-flex rounded-[6px] border border-black px-4 py-2 text-sm font-bold transition hover:bg-neutral-100"
								>
									Open
								</a>
							</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{:else}
		<div class="border border-dashed border-neutral-300 px-8 py-16 text-center">
			<h2 class="text-xl font-bold">No products found</h2>
		</div>
	{/if}
</div>