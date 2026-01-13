import axios from 'axios';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { IIntegrationInstance, defaultValue } from 'app/shared/model/integration-instance.model';

const initialState = {
  loading: false,
  errorMessage: null as string | null,
  entities: [] as ReadonlyArray<IIntegrationInstance>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/integration-instances';

export const getEntities = createAsyncThunk('integrationInstance/fetch_entity_list', async () => {
  const requestUrl = `${apiUrl}?sort=name,asc`;
  return axios.get<IIntegrationInstance[]>(requestUrl);
});

export const getEntitiesByIntegrationCode = createAsyncThunk('integrationInstance/fetch_by_integration_code', async (code: string) => {
  const requestUrl = `${apiUrl}/by-integration/${code}`;
  return axios.get<IIntegrationInstance[]>(requestUrl);
});

export const getEntity = createAsyncThunk('integrationInstance/fetch_entity', async (id: string | number) => {
  const requestUrl = `${apiUrl}/${id}`;
  return axios.get<IIntegrationInstance>(requestUrl);
});

export const createEntity = createAsyncThunk('integrationInstance/create_entity', async (entity: IIntegrationInstance, thunkAPI) => {
  const result = await axios.post<IIntegrationInstance>(apiUrl, entity);
  thunkAPI.dispatch(getEntities());
  return result;
});

export const updateEntity = createAsyncThunk('integrationInstance/update_entity', async (entity: IIntegrationInstance, thunkAPI) => {
  const result = await axios.put<IIntegrationInstance>(`${apiUrl}/${entity.id}`, entity);
  thunkAPI.dispatch(getEntities());
  return result;
});

export const deleteEntity = createAsyncThunk('integrationInstance/delete_entity', async (id: string | number, thunkAPI) => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await axios.delete<IIntegrationInstance>(requestUrl);
  thunkAPI.dispatch(getEntities());
  return result;
});

export type IntegrationInstanceState = Readonly<typeof initialState>;

export const IntegrationInstanceSlice = createSlice({
  name: 'integrationInstance',
  initialState: initialState as IntegrationInstanceState,
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
        (action): action is { type: string; payload: { data: IIntegrationInstance[] } } =>
          action.type.endsWith('/fulfilled') &&
          (action.type.includes('getEntities') || action.type.includes('getEntitiesByIntegrationCode')),
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
        (action): action is { type: string; payload: { data: IIntegrationInstance } } =>
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

export const { reset } = IntegrationInstanceSlice.actions;

export default IntegrationInstanceSlice.reducer;
