using ASP.NET.Models;

namespace ASP.NET.Controllers;

public class AuthService
{
    private MemoryGameContext db;

    public AuthService(MemoryGameContext db)
    {
        this.db = db;
    }

    public User Authenticate(string username, string password)
    {
        return db.User.FirstOrDefault(user =>
            user.Username.ToLower() == username.ToLower() && user.Password == password);
       
    }
    
    
}