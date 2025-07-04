using ASP.NET.DTO;
using ASP.NET.Models;

namespace ASP.NET.Controllers;

public class ScoreService
{
    private readonly MemoryGameContext db;

    public ScoreService(MemoryGameContext db)
    {
        this.db = db;
    }

    public void AddScore(ScoreRequest scoreRequest)
    {
        var user = db.User.FirstOrDefault(u => u.Id == scoreRequest.UserId);
        if (user == null) throw new Exception("User not found");

        var score = new Score
        {
            Id = Guid.NewGuid().ToString(),
            Time = scoreRequest.Time,
            User = user
        };
        
        db.Score.Add(score);
        db.SaveChanges();
    }
}