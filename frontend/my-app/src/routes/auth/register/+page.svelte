<script lang="ts">
	import type { ActionData, PageData } from './$types';

	let { data, form }: { data: PageData; form: ActionData } = $props();
</script>

<section class="mx-auto max-w-md space-y-6">
	<div class="space-y-2">
		<p class="text-sm font-semibold uppercase tracking-[0.2em] text-neutral-500">Auth</p>
		<h1 class="text-3xl font-black tracking-tight">Register</h1>
	</div>

	<form method="POST" class="space-y-4 rounded-2xl border border-neutral-900 p-6">
		<input type="hidden" name="redirectTo" value={form?.values?.redirectTo ?? data.redirectTo} />

		<div class="space-y-2">
			<label class="text-sm font-medium" for="displayName">Display name</label>
			<input
				id="displayName"
				name="displayName"
				type="text"
				required
				value={form?.values?.displayName ?? ''}
				class="w-full rounded-xl border border-neutral-300 px-4 py-3 outline-none transition focus:border-neutral-900"
			/>
		</div>

		<div class="space-y-2">
			<label class="text-sm font-medium" for="email">Email</label>
			<input
				id="email"
				name="email"
				type="email"
				required
				value={form?.values?.email ?? ''}
				class="w-full rounded-xl border border-neutral-300 px-4 py-3 outline-none transition focus:border-neutral-900"
			/>
		</div>

		<div class="space-y-2">
			<label class="text-sm font-medium" for="password">Password</label>
			<input
				id="password"
				name="password"
				type="password"
				required
				class="w-full rounded-xl border border-neutral-300 px-4 py-3 outline-none transition focus:border-neutral-900"
			/>
		</div>

		{#if form?.message}
			<p class="rounded-xl border border-red-300 bg-red-50 px-4 py-3 text-sm text-red-700">
				{form.message}
			</p>
		{/if}

		<button
			type="submit"
			class="w-full rounded-xl bg-neutral-950 px-4 py-3 font-semibold text-white transition hover:bg-neutral-800"
		>
			Create account
		</button>
	</form>
</section>