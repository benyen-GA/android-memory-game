namespace ASP.NET.Models;

public class User
{

  public User()
  {
    Id = Guid.NewGuid().ToString();
  }
  public string Id { get; set; }
  public string Username { get; set; }
  public string Password { get; set; }
  public Boolean IsPaidUser { get; set; }
  
  // to establish 1 to 1 relationship w User and Leaderboard once Leaderboard is up
  
  public virtual ICollection<Score> Score { get; set; }
  
}