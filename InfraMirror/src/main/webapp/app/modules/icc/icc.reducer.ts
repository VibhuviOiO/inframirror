import axios from 'axios';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';

const apiUrl = 'api/control-integrations';

export const getIntegrations = createAsyncThunk('icc/fetch_integrations', async () => {
  const response = await axios.get<any[]>(apiUrl);
  return response.data;
});

const initialState = {
  loading: false,
  integrations: [] as any[],
  errorMessage: null as string | null,
};

export const ICCSlice = createSlice({
  name: 'icc',
  initialState,
  reducers: {},
  extraReducers(builder) {
    builder
      .addCase(getIntegrations.pending, state => {
        state.loading = true;
      })
      .addCase(getIntegrations.fulfilled, (state, action) => {
        state.loading = false;
        state.integrations = action.payload;
      })
      .addCase(getIntegrations.rejected, (state, action) => {
        state.loading = false;
        state.errorMessage = action.error.message;
      });
  },
});

export default ICCSlice.reducer;
