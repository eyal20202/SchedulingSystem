import { useState, useEffect } from 'react';
import apiClient from '../services/api';
import { Schedule, TaskDefinition } from '../types';

export const useSchedules = () => {
  const [schedules, setSchedules] = useState<Schedule[]>([]);
  const [tasks, setTasks] = useState<TaskDefinition[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const [tasksResponse, schedulesResponse] = await Promise.all([
        apiClient.getTasks(),
        apiClient.getSchedules(),
      ]);
      setTasks(tasksResponse);
      setSchedules(schedulesResponse.content);
    } catch (err) {
      setError('Failed to load data');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  const createSchedule = async (schedule: Schedule) => {
    setIsLoading(true);
    setError(null);
    try {
      console.log('Creating schedule:', schedule);
      const result = await apiClient.createSchedule(schedule);
      console.log('Schedule created:', result);
      await loadData();
      return { success: true, message: 'Schedule created successfully' };
    } catch (err: any) {
      const errorMsg = err.response?.data?.message || 'Failed to create schedule';
      console.error('Create schedule error:', err);
      setError(errorMsg);
      return { success: false, message: errorMsg };
    } finally {
      setIsLoading(false);
    }
  };

  const updateSchedule = async (id: number, schedule: Schedule) => {
    setIsLoading(true);
    try {
      await apiClient.updateSchedule(id, schedule);
      await loadData();
      return { success: true, message: 'Schedule updated successfully' };
    } catch (err) {
      setError('Failed to update schedule');
      console.error(err);
      return { success: false, message: 'Failed to update schedule' };
    } finally {
      setIsLoading(false);
    }
  };

  const deleteSchedule = async (id: number) => {
    setIsLoading(true);
    setError(null);
    try {
      console.log('Deleting schedule:', id);
      await apiClient.deleteSchedule(id);
      console.log('Schedule deleted successfully');
      await loadData();
      return { success: true, message: 'Schedule deleted successfully' };
    } catch (err: any) {
      const errorMsg = err.response?.data?.message || 'Failed to delete schedule';
      console.error('Delete schedule error:', err);
      setError(errorMsg);
      return { success: false, message: errorMsg };
    } finally {
      setIsLoading(false);
    }
  };

  const toggleSchedule = async (id: number) => {
    setIsLoading(true);
    try {
      await apiClient.toggleSchedule(id);
      await loadData();
      return { success: true, message: 'Schedule status updated' };
    } catch (err) {
      setError('Failed to toggle schedule');
      console.error(err);
      return { success: false, message: 'Failed to toggle schedule' };
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return {
    schedules,
    tasks,
    isLoading,
    error,
    createSchedule,
    updateSchedule,
    deleteSchedule,
    toggleSchedule,
    loadData,
  };
};
