import axios from 'axios';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { IControlIntegration, defaultValue } from 'app/shared/model/control-integration.model';

const initialState = {
  loading: false,
  errorMessage: null as string | null,
  entities: [] as ReadonlyArray<IControlIntegration>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/control-integrations';

export const getEntities = createAsyncThunk('controlIntegration/fetch_entity_list', async () => {
  const requestUrl = `${apiUrl}?sort=name,asc`;
  return axios.get<IControlIntegration[]>(requestUrl);
});

export const getEntity = createAsyncThunk('controlIntegration/fetch_entity', async (id: string | number) => {
  const requestUrl = `${apiUrl}/${id}`;
  return axios.get<IControlIntegration>(requestUrl);
});

export const getEntityByCode = createAsyncThunk('controlIntegration/fetch_entity_by_code', async (code: string) => {
  const requestUrl = `${apiUrl}/code/${code}`;
  return axios.get<IControlIntegration>(requestUrl);
});

export const createEntity = createAsyncThunk('controlIntegration/create_entity', async (entity: IControlIntegration, thunkAPI) => {
  const result = await axios.post<IControlIntegration>(apiUrl, entity);
  thunkAPI.dispatch(getEntities());
  return result;
});

export const updateEntity = createAsyncThunk('controlIntegration/update_entity', async (entity: IControlIntegration, thunkAPI) => {
  const result = await axios.put<IControlIntegration>(`${apiUrl}/${entity.id}`, entity);
  thunkAPI.dispatch(getEntities());
  return result;
});

export const deleteEntity = createAsyncThunk('controlIntegration/delete_entity', async (id: string | number, thunkAPI) => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await axios.delete<IControlIntegration>(requestUrl);
  thunkAPI.dispatch(getEntities());
  return result;
});

export type ControlIntegrationState = Readonly<typeof initialState>;

export const ControlIntegrationSlice = createSlice({
  name: 'controlIntegration',
  initialState: initialState as ControlIntegrationState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(getEntityByCode.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = defaultValue;
      })
      .addMatcher(
        action => action.type.endsWith('/pending'),
        state => {
          state.errorMessage = null;
          state.updateSuccess = false;
          state.loading = true;
          state.updating = true;
        },
      )
      .addMatcher(
        (action): action is { type: string; error: { message: string } } => action.type.endsWith('/rejected'),
        (state, action) => {
          state.loading = false;
          state.updating = false;
          state.updateSuccess = false;
          state.errorMessage = action.error.message;
        },
      )
      .addMatcher(
        (action): action is { type: string; payload: { data: IControlIntegration[] } } =>
          action.type.endsWith('/fulfilled') && action.type.includes('getEntities'),
        (state, action) => {
          const { data } = action.payload;
          return {
            ...state,
            loading: false,
            entities: Array.isArray(data) ? data : [],
          };
        },
      )
      .addMatcher(
        (action): action is { type: string; payload: { data: IControlIntegration } } =>
          action.type.endsWith('/fulfilled') && (action.type.includes('createEntity') || action.type.includes('updateEntity')),
        (state, action) => {
          state.updating = false;
          state.loading = false;
          state.updateSuccess = true;
          state.entity = action.payload.data;
        },
      );
  },
});

export const { reset } = ControlIntegrationSlice.actions;

export default ControlIntegrationSlice.reducer;
