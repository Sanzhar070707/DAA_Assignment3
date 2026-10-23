import os
import pandas as pd
import matplotlib.pyplot as plt

os.makedirs('results/plots', exist_ok=True)
df = pd.read_csv('results/results.csv')

# Разделяем на Sparse (m=4n) и Dense (m=20n)
df['density'] = df.apply(lambda row: 'Sparse (4n)' if row['m'] == 4 * row['n'] else 'Dense (20n)', axis=1)

# --- Plot 1: Operations vs n (Log-Log Axes) ---
plt.figure(figsize=(10, 6))
for algo in df['algorithm'].unique():
    for density in ['Sparse (4n)', 'Dense (20n)']:
        subset = df[(df['algorithm'] == algo) & (df['density'] == density)].sort_values('n')
        ls = '-' if density == 'Sparse (4n)' else '--'
        plt.loglog(subset['n'], subset['edge_scans'], marker='o', linestyle=ls, label=f"{algo} ({density})")

plt.xlabel('n (Vertices - log scale)')
plt.ylabel('Edge Scans (log scale)')
plt.title('Operations vs n (Log-Log Axes)')
plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left')
plt.grid(True, which="both", ls="--")
plt.tight_layout()
plt.savefig('results/plots/operations_vs_n.png')
plt.close()

# --- Plot 2: Scans per edge (edge_scans / m vs n) ---
plt.figure(figsize=(10, 6))
df['scans_per_edge'] = df['edge_scans'] / df['m']

for algo in df['algorithm'].unique():
    subset = df[df['algorithm'] == algo].groupby('n')['scans_per_edge'].mean().reset_index()
    plt.plot(subset['n'], subset['scans_per_edge'], marker='o', label=algo)

plt.xlabel('n (Vertices)')
plt.ylabel('Edge Scans / m')
plt.title('Scans per Edge vs n (Flat Line = Linear Work)')
plt.ylim(0, 2.5)
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig('results/plots/scans_per_edge.png')
plt.close()

# --- Plot 3: Share of edges Kruskal examines ---
plt.figure(figsize=(10, 6))
kruskal_df = df[df['algorithm'] == 'Kruskal']

sparse = kruskal_df[kruskal_df['density'] == 'Sparse (4n)'].sort_values('n')
dense = kruskal_df[kruskal_df['density'] == 'Dense (20n)'].sort_values('n')

plt.plot(sparse['n'], sparse['edge_scans'] / sparse['m'], marker='o', label='Kruskal Sparse (m=4n)')
plt.plot(dense['n'], dense['edge_scans'] / dense['m'], marker='o', label='Kruskal Dense (m=20n)')

plt.xlabel('n (Vertices)')
plt.ylabel('Edge Scans / m')
plt.title('Share of Edges Kruskal Examines')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig('results/plots/kruskal_edge_share.png')
plt.close()

print("Графики перегенерированы идеально!")