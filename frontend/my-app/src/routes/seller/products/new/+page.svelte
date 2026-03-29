<script lang="ts">
	import type { ActionData, PageData } from './$types';
	import SellerProductForm from '$lib/components/seller/SellerProductForm.svelte';

	let { data, form }: { data: PageData; form: ActionData } = $props();
</script>

<div class="space-y-8">
	<div>
		<h2 class="text-3xl font-black tracking-tight text-[#1f1f1f]">New Product</h2>
		<p class="mt-2 text-sm leading-6 text-neutral-600">
			Create a new product draft and continue with archive/images after creation.
		</p>
	</div>

	<SellerProductForm
		action="?"
		submitLabel="Create Draft"
		availableTags={data.availableTags}
		values={{
			title: form?.values?.title ?? '',
			shortDescription: form?.values?.shortDescription ?? '',
			description: form?.values?.description ?? '',
			priceUsd: form?.values?.priceUsd ?? '',
			selectedTagSlugs: form?.values?.selectedTagSlugs ?? []
		}}
		message={form?.message ?? null}
		success={false}
	/>
</div>