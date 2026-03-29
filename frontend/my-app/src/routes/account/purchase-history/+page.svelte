<script lang="ts">
	import type { PageData } from './$types';
	import { formatDateTime } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Purchase History</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			All purchases made on your account.
		</p>
	</div>

	{#if data.items.length > 0}
		<div class="overflow-x-auto border border-neutral-200">
			<table class="min-w-full border-collapse text-left">
				<thead class="bg-neutral-50">
					<tr>
						<th class="px-5 py-4 text-sm font-bold">ORDER num</th>
						<th class="px-5 py-4 text-sm font-bold">NAME</th>
						<th class="px-5 py-4 text-sm font-bold">DATE</th>
						<th class="px-5 py-4 text-sm font-bold">STATUS</th>
					</tr>
				</thead>
				<tbody>
					{#each data.items as item}
						<tr class="border-t border-neutral-200">
							<td class="px-5 py-4 text-sm">{item.orderNumber}</td>
							<td class="px-5 py-4 text-sm font-medium">
								<a href={`/products/${item.productSlug}`} class="hover:underline">
									{item.productName}
								</a>
							</td>
							<td class="px-5 py-4 text-sm">{formatDateTime(item.date)}</td>
							<td class="px-5 py-4 text-sm">{item.status}</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
	{:else}
		<div class="border border-dashed border-neutral-300 px-8 py-16 text-center">
			<h2 class="text-xl font-bold">No purchases yet</h2>
			<p class="mt-2 text-sm text-neutral-600">
				When you buy products, they will appear here.
			</p>
		</div>
	{/if}
</div>