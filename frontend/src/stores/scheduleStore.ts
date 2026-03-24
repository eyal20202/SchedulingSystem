import { create } from 'zustand';
import { Schedule, TaskDefinition } from '../types';

interface ScheduleStore {
  schedules: Schedule[];
  tasks: TaskDefinition[];
  isLoading: boolean;
  error: string | null;
  setSchedules: (schedules: Schedule[]) => void;
  setTasks: (tasks: TaskDefinition[]) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  addSchedule: (schedule: Schedule) => void;
  removeSchedule: (id: number) => void;
  updateSchedule: (schedule: Schedule) => void;
}

export const useScheduleStore = create<ScheduleStore>((set) => ({
  schedules: [],
  tasks: [],
  isLoading: false,
  error: null,
  setSchedules: (schedules) => set({ schedules }),
  setTasks: (tasks) => set({ tasks }),
  setLoading: (loading) => set({ isLoading: loading }),
  setError: (error) => set({ error }),
  addSchedule: (schedule) =>
    set((state) => ({
      schedules: [...state.schedules, schedule],
    })),
  removeSchedule: (id) =>
    set((state) => ({
      schedules: state.schedules.filter((s) => s.id !== id),
    })),
  updateSchedule: (updatedSchedule) =>
    set((state) => ({
      schedules: state.schedules.map((s) =>
        s.id === updatedSchedule.id ? updatedSchedule : s
      ),
    })),
}));
