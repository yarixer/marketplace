<script lang="ts">
	import { page } from '$app/state';
</script>

<section class="mx-auto max-w-2xl space-y-4 py-20 text-center">
	<p class="text-sm font-semibold uppercase tracking-[0.18em] text-neutral-500">
		Error {page.status}
	</p>
	<h1 class="text-4xl font-black tracking-tight">Something went wrong</h1>
	<p class="text-neutral-600">
		{page.error?.message ?? 'Unexpected error'}
	</p>

	<div class="pt-4">
		<a
			href="/"
			class="inline-flex rounded-full border border-black px-5 py-3 font-semibold transition hover:bg-neutral-100"
		>
			Go home
		</a>
	</div>
</section>