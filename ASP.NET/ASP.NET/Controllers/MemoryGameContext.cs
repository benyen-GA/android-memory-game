using ASP.NET.Models;
using Microsoft.EntityFrameworkCore;

namespace ASP.NET.Controllers;

public class MemoryGameContext : DbContext
{
    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder) { optionsBuilder.UseMySql(
            // provides database connection-string
            "server=localhost;user=root;password=password;database=memorygame;",
            new MySqlServerVersion(new Version(8, 0, 36))
        );
        optionsBuilder.UseLazyLoadingProxies();
    }
    
    
    // our database tables
    public DbSet<User> User { get; set; }
    
}