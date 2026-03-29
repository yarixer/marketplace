<script lang="ts">
	import type { Snippet } from 'svelte';
	import type { LayoutData } from './$types';
	import { page } from '$app/state';
	import AdminTabs from '$lib/components/admin/AdminTabs.svelte';

	let { data: _data, children }: { data: LayoutData; children: Snippet } = $props();
</script>

<div class="full-bleed grid-page-bg flex-1">
	<div class="mx-auto max-w-[1280px] px-6 py-10 md:py-12">
		<div class="space-y-6">
			<div>
				<h1 class="text-[44px] font-black tracking-tight text-[#1f1f1f]">Admin Panel</h1>
			</div>

			<div class="border border-neutral-200 bg-white shadow-[0_10px_35px_rgba(0,0,0,0.04)]">
				<div class="border-b border-neutral-200">
					<AdminTabs pathname={page.url.pathname} />
				</div>

				<section class="px-5 py-8 md:px-10 md:py-10">
					{@render children()}
				</section>
			</div>
		</div>
	</div>
</div>