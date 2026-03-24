import axios, { AxiosInstance, AxiosError } from 'axios';
import { API_CONFIG } from '../config/api';
import { Schedule, TaskDefinition, PaginatedResponse } from '../types';

class ApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: API_CONFIG.BASE_URL,
      timeout: API_CONFIG.TIMEOUT,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor
    this.client.interceptors.request.use(
      (config: any) => {
        console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
        return config;
      },
      (error: any) => {
        console.error('Request error:', error);
        return Promise.reject(error);
      }
    );

    // Add error interceptor
    this.client.interceptors.response.use(
      (response: any) => {
        console.log(`API Response: ${response.status} ${response.config.url}`);
        return response;
      },
      (error: AxiosError) => {
        const message = String(error.response?.data || error.message).replace(/[\r\n]/g, '');
        console.error('API Error:', message);
        console.error('Error details:', error.response);
        return Promise.reject(error);
      }
    );
  }

  // Schedule endpoints
  async getSchedules(page: number = 0, size: number = 10): Promise<PaginatedResponse<Schedule>> {
    const response = await this.client.get(`/api/schedules?page=${page}&size=${size}`);
    return response.data;
  }

  async getScheduleById(id: number): Promise<Schedule> {
    const response = await this.client.get(`/api/schedules/${id}`);
    return response.data;
  }

  async createSchedule(schedule: Schedule): Promise<Schedule> {
    const response = await this.client.post('/api/schedules', schedule);
    return response.data;
  }

  async updateSchedule(id: number, schedule: Schedule): Promise<Schedule> {
    const response = await this.client.put(`/api/schedules/${id}`, schedule);
    return response.data;
  }

  async deleteSchedule(id: number): Promise<void> {
    await this.client.delete(`/api/schedules/${id}`);
  }

  async toggleSchedule(id: number): Promise<Schedule> {
    const response = await this.client.post(`/api/schedules/${id}/toggle`);
    return response.data;
  }

  // Task endpoints
  async getTasks(): Promise<TaskDefinition[]> {
    const response = await this.client.get('/api/tasks');
    return response.data;
  }

  async getTaskById(taskId: string): Promise<TaskDefinition> {
    const response = await this.client.get(`/api/tasks/${taskId}`);
    return response.data;
  }
}

export default new ApiClient();
