export interface UserCreateDto {
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
}

export interface UserDetailDto {
  id: number;
  svnr?: string;
  email: string;
  firstname: string;
  lastname: string;
  password: string;
}

export interface UserLoginDto {
  email: string;
  password: string;
}
