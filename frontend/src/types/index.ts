export enum ScheduleFrequencyType {
  ONE_TIME = 'ONE_TIME',
  RECURRING_MINUTES = 'RECURRING_MINUTES',
  RECURRING_HOURS = 'RECURRING_HOURS',
  RECURRING_DAYS = 'RECURRING_DAYS',
  WEEKLY = 'WEEKLY',
  CRON_EXPRESSION = 'CRON_EXPRESSION',
}

export enum ParameterType {
  STRING = 'STRING',
  NUMBER = 'NUMBER',
  BOOLEAN = 'BOOLEAN',
  DATE = 'DATE',
}

export interface ParameterDefinition {
  name: string;
  type: ParameterType;
  required: boolean;
  defaultValue?: string;
  description?: string;
}

export interface TaskDefinition {
  id: string;
  name: string;
  description: string;
  parameters: ParameterDefinition[];
}

export interface ScheduleParameter {
  id?: number;
  parameterName: string;
  parameterValue: string;
  parameterType: ParameterType;
  required: boolean;
}

export interface Schedule {
  id?: number;
  name: string;
  description?: string;
  taskId: string;
  frequencyType: ScheduleFrequencyType;
  frequencyValue?: number;
  cronExpression?: string;
  weekDays?: string;
  oneTimeExecutionTime?: string;
  enabled: boolean;
  nextExecutionTime?: string;
  lastExecutionTime?: string;
  status?: string;
  parameters: ScheduleParameter[];
  createdAt?: string;
  updatedAt?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  size: number;
}

export interface ErrorResponse {
  status: number;
  message: string;
  timestamp: number;
  path: string;
}

export const WEEKDAY_OPTIONS = [
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY',
] as const;

export type Weekday = (typeof WEEKDAY_OPTIONS)[number];