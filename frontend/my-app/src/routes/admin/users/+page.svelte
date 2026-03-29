<script lang="ts">
	import type { PageData } from './$types';
	import { formatMoney } from '$lib/utils/format';

	let { data }: { data: PageData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">Users</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			View all users, roles, seller status and balances.
		</p>
	</div>

	{#if data.items.length > 0}
		<div class="overflow-x-auto border border-neutral-200">
			<table class="min-w-full border-collapse text-left">
				<thead class="bg-neutral-50">
					<tr>
						<th class="px-5 py-4 text-sm font-bold">User</th>
						<th class="px-5 py-4 text-sm font-bold">Roles</th>
						<th class="px-5 py-4 text-sm font-bold">Enabled</th>
						<th class="px-5 py-4 text-sm font-bold">Balance</th>
						<th class="px-5 py-4 text-sm font-bold">Action</th>
					</tr>
				</thead>
				<tbody>
					{#each data.items as item}
						<tr class="border-t border-neutral-200">
							<td class="px-5 py-4">
								<div class="space-y-1">
									<p class="font-bold">{item.displayName}</p>
									<p class="text-sm text-neutral-500">{item.email}</p>
								</div>
							</td>
							<td class="px-5 py-4 text-sm">{item.roles.join(', ')}</td>
							<td class="px-5 py-4 text-sm">{item.enabled ? 'Yes' : 'No'}</td>
							<td class="px-5 py-4 text-sm">
								{formatMoney(item.availableMinor, 'USD')}
							</td>
							<td class="px-5 py-4">
								<a
									href={`/admin/users/${item.userId}`}
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
			<h2 class="text-xl font-bold">No users found</h2>
		</div>
	{/if}
</div>