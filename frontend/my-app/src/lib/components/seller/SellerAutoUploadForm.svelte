<script lang="ts">
	import { enhance } from '$app/forms';

	let {
		action,
		title,
		description,
		inputId,
		inputName,
		accept,
		multiple = false,
		dropLabel,
		dropHint,
		message = null,
		success = false,
		currentTitle = null,
		currentSubtitle = null
	}: {
		action: string;
		title: string;
		description: string;
		inputId: string;
		inputName: string;
		accept: string;
		multiple?: boolean;
		dropLabel: string;
		dropHint: string;
		message?: string | null;
		success?: boolean;
		currentTitle?: string | null;
		currentSubtitle?: string | null;
	} = $props();

	let formElement: HTMLFormElement | null = null;
	let isSubmitting = $state(false);

	function handleChange(event: Event) {
		const target = event.currentTarget as HTMLInputElement;

		if (!target.files || target.files.length === 0) {
			return;
		}

		formElement?.requestSubmit();
	}
</script>

<form
	method="POST"
	action={action}
	enctype="multipart/form-data"
	bind:this={formElement}
	use:enhance={() => {
		isSubmitting = true;

		return async ({ update }) => {
			await update();
			isSubmitting = false;
		};
	}}
	class="border border-neutral-200 bg-white p-6 shadow-[0_10px_35px_rgba(0,0,0,0.04)]"
>
	<div class="space-y-2">
		<h3 class="text-2xl font-black tracking-tight text-[#1f1f1f]">{title}</h3>
		<p class="text-sm leading-6 text-neutral-600">{description}</p>
	</div>

	{#if currentTitle}
		<div class="mt-5 rounded-[8px] border border-neutral-200 bg-neutral-50 px-4 py-4">
			<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-400">Current File</p>
			<p class="mt-2 text-sm font-bold text-neutral-900">{currentTitle}</p>
			{#if currentSubtitle}
				<p class="mt-1 text-sm text-neutral-600">{currentSubtitle}</p>
			{/if}
		</div>
	{/if}

	<div class="mt-5 space-y-3">
		<label
			for={inputId}
			class="flex cursor-pointer flex-col items-center justify-center gap-2 rounded-[8px] border-2 border-dashed border-neutral-400 bg-neutral-50 px-6 py-8 text-center transition hover:bg-neutral-100"
		>
			<span class="text-sm font-bold text-neutral-800">
				{isSubmitting ? 'Uploading…' : dropLabel}
			</span>
			<span class="text-xs text-neutral-500">{dropHint}</span>
		</label>

		<input
			id={inputId}
			name={inputName}
			type="file"
			accept={accept}
			multiple={multiple}
			onchange={handleChange}
			class="block w-full text-sm file:mr-4 file:rounded-[6px] file:border-0 file:bg-neutral-950 file:px-4 file:py-2 file:text-sm file:font-bold file:text-white hover:file:bg-neutral-800"
		/>

		<button type="submit" class="hidden">Upload</button>
	</div>

	{#if message}
		<p class={`mt-5 px-4 py-3 text-sm ${success ? 'bg-green-50 text-green-700' : 'bg-red-50 text-red-700'}`}>
			{message}
		</p>
	{/if}
</form>