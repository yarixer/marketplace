export interface PageResponse<T> {
	items: T[];
	page: number;
	size: number;
	totalElements: number;
	totalPages: number;
	first: boolean;
	last: boolean;
}

export interface ProductCardResponse {
	productId: number;
	slug: string;
	title: string;
	shortDescription: string;
	priceMinor: number;
	currency: string;
	sellerName: string;
	coverImageUrl: string | null;
	ratingScore: number;
	positiveVotes: number;
	negativeVotes: number;
}

export interface PublicProductTagResponse {
	id: number;
	name: string;
	slug: string;
}

export interface PublicProductImageResponse {
	url: string;
	sortOrder: number;
	cover: boolean;
}

export interface PublicProductVoteSummaryResponse {
	positiveCount: number;
	negativeCount: number;
	score: number;
}

export interface PublicProductSellerResponse {
	publicName: string;
	slug: string;
}

export interface PublicProductDetailsResponse {
	productId: number;
	currentLiveRevisionId: number;
	slug: string;
	title: string;
	shortDescription: string;
	description: string;
	priceMinor: number;
	currency: string;
	seller: PublicProductSellerResponse;
	tags: PublicProductTagResponse[];
	images: PublicProductImageResponse[];
	voteSummary: PublicProductVoteSummaryResponse;
	archiveAttached: boolean;
}