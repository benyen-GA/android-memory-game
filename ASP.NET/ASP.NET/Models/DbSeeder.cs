using ASP.NET.Controllers;

namespace ASP.NET.Models;

public static class DbSeeder
{
    public static void Seed(MemoryGameContext context)
    {
        //check if any user exists in db table, if not create new users
        if (!context.User.Any())
        {
            var FreeUser = new User
            {
                Username = "freeUser",
                Password = "free",
                IsPaidUser = false
            };

            var PaidUser = new User
            {
                Username = "paidUser",
                Password = "paid",
                IsPaidUser = true,
            };

            context.User.Add(FreeUser);
            context.User.Add(PaidUser);
            context.SaveChanges();
        }
    }
    
}