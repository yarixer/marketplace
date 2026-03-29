<script lang="ts">
	let {
		label,
		value,
		tone = 'neutral'
	}: {
		label: string;
		value: string;
		tone?: 'neutral' | 'green' | 'amber' | 'red';
	} = $props();

	const toneClasses = {
		neutral: 'bg-white',
		green: 'bg-green-50',
		amber: 'bg-amber-50',
		red: 'bg-red-50'
	};
</script>

<div class={`border border-neutral-200 p-5 ${toneClasses[tone]}`}>
	<p class="text-xs font-semibold uppercase tracking-[0.14em] text-neutral-500">{label}</p>
	<p class="mt-3 text-3xl font-black tracking-tight">{value}</p>
</div>