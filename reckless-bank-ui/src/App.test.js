import { render, screen, waitFor } from '@testing-library/react';
import App from './App';

beforeEach(() => {
  global.fetch = jest.fn(() =>
    Promise.resolve({
      json: () => Promise.resolve([]),
    })
  );
});

afterEach(() => {
  jest.restoreAllMocks();
});

test('renders the bank app heading', async () => {
  render(<App />);
  const heading = screen.getByRole('heading', { name: /fast & reckless bank/i });
  expect(heading).toBeInTheDocument();
  await waitFor(() => expect(global.fetch).toHaveBeenCalledWith('/api/accounts'));
});
