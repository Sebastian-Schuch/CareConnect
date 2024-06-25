export interface ApiKeyDto {
  id: number,
  description: string;
  created: Date;
}

export interface ApiKeyDtoCreate {
  description: string;
}

export interface ApiKeyPageDto{
  apiKeys: ApiKeyDto[],
  totalElements: number
}

export interface ApiKeyDtoFirst{
  id: number,
  description: string;
  created: Date;
  apiKey: string;
}
