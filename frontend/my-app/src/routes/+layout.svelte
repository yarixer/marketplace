<script lang="ts">
	import '../app.css';
	import type { Snippet } from 'svelte';
	import type { LayoutData } from './$types';
	import AppFooter from '$lib/components/layout/AppFooter.svelte';
	import AppHeader from '$lib/components/layout/AppHeader.svelte';

	let { data, children }: { data: LayoutData; children: Snippet } = $props();
</script>

<svelte:head>
	<title>Models</title>
	<meta name="viewport" content="width=device-width, initial-scale=1" />
</svelte:head>

<div class="flex min-h-screen flex-col bg-white text-neutral-950">
	<AppHeader session={data.session} />

	<main class="flex flex-1 flex-col overflow-x-clip">
		{@render children()}
	</main>

	<AppFooter />
</div>