export interface AuthResponse {
  token: string;
  refreshToken: string;
  userId: number;
  studentNo: string;
  nickname: string;
  role: string;
  admin: boolean;
}
