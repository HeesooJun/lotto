class Solution {
    // 방향: 위, 오른쪽, 아래, 왼쪽
    private final int[] dr = {-1, 0, 1, 0};
    private final int[] dc = {0, 1, 0, -1};

    // 각 선로가 연결하는 방향을 비트로 표현
    private final int[] masks = {0, 10, 5, 15, 9, 3, 6, 12};

    private int[][] grid, placed, used;
    private int n, m, answer;

    public int solution(int[][] grid) {
        this.grid = grid;
        n = grid.length;
        m = grid[0].length;

        placed = new int[n][m];
        used = new int[n][m];
        answer = 0;

        int remaining = 0;
        for (int[] row : grid) {
            for (int tile : row) {
                if (tile > 0) {
                    remaining += tile == 3 ? 2 : 1;
                }
            }
        }

        dfs(0, 0, 1, remaining);
        return answer;
    }

    private void dfs(int r, int c, int dir, int remaining) {
        if (r < 0 || r >= n || c < 0 || c >= m
                || grid[r][c] == -1) {
            return;
        }

        int entry = (dir + 2) % 4;
        int inBit = 1 << entry;

        if (grid[r][c] == 0) {
            int previous = placed[r][c];

            if (previous == 0) {
                // 빈칸에서는 직진 또는 회전
                for (int out = 0; out < 4; out++) {
                    if (out == entry) continue;

                    int ports = inBit | (1 << out);
                    placed[r][c] = ports;
                    move(r, c, out, ports, remaining);
                }
                placed[r][c] = 0;

            } else if ((previous == 5 || previous == 10)
                    && (previous & inBit) == 0) {
                // 직선으로 지났던 칸을 수직으로 다시 지나면 십자
                placed[r][c] = 15;
                move(r, c, dir, inBit | (1 << dir), remaining);
                placed[r][c] = previous;
            }
            return;
        }

        int mask = masks[grid[r][c]];
        if ((mask & inBit) == 0) return;

        int out = grid[r][c] == 3
                ? dir
                : Integer.numberOfTrailingZeros(mask ^ inBit);

        move(r, c, out, inBit | (1 << out), remaining - 1);
    }

    private void move(int r, int c, int out,
                      int ports, int remaining) {
        if ((used[r][c] & ports) != 0) return;

        used[r][c] |= ports;

        if (r == n - 1 && c == m - 1) {
            if (remaining == 0) answer++;
        } else {
            dfs(r + dr[out], c + dc[out], out, remaining);
        }

        used[r][c] ^= ports;
    }
}